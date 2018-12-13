/*
 * This file is part of ZipExtractor.
 * Copyright (C) 2016-2018 Daniel D. Scalzi <https://github.com/dscalzi/ZipExtractor>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dscalzi.zipextractor.core;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.dscalzi.zipextractor.core.managers.MessageManager;
import com.dscalzi.zipextractor.core.provider.TypeProvider;
import com.dscalzi.zipextractor.core.util.ICommandSender;
import com.dscalzi.zipextractor.core.util.OpTuple;

public class ZCompressor {

    private static List<String> SUPPORTED;

    public static void asyncCompress(ICommandSender sender, File src, File dest, boolean log, final boolean override) {
        final MessageManager mm = MessageManager.inst();
        // If the source does not exist, abort.
        if (!src.exists()) {
            mm.sourceNotFound(sender, src.getAbsolutePath());
            return;
        }
        
        Path destNorm = dest.toPath().toAbsolutePath().normalize();
        
        // Split at first dot.
        String[] destSplit = destNorm.getFileName().toString().split("\\.", 2);
        
        // We need an extension.
        if(destSplit.length < 2) {
            mm.invalidCompressionExtension(sender);
            return;
        }
        
        String[] destExts = destSplit[1].split("\\.");
        
        Deque<OpTuple> pDeque = new ArrayDeque<OpTuple>();
        if(destExts.length < 2) {
            pDeque.push(new OpTuple(src, dest));
        } else {
            File dTemp = dest;
            File sTemp = null;
            
            String pth = destNorm.toString();
            
            for(int i=destExts.length-1; i>=0; i--) {
                if(supportedExtensions().contains(destExts[i].toLowerCase())) {
                    pth = pth.substring(0, pth.length()-destExts[i].length()-1);
                    sTemp = new File(pth);
                    pDeque.push(new OpTuple(i == 0 ? src : sTemp, dTemp));
                    dTemp = sTemp;
                } else {
                    pDeque.peek().setSrc(src);
                    break;
                }
            }
        }
        
        Runnable task = null;
        int c = 0;
        boolean piped = false;
        final Runnable[] pipes = new Runnable[pDeque.size()];
        for (final OpTuple e : pDeque) {
            for (final TypeProvider p : TypeProvider.getProviders()) {
                if (p.destValidForCompression(e.getDest())) {
                    if (p.srcValidForCompression(e.getSrc())) {
                        final boolean interOp = c != pDeque.size()-1;
                        
                        if (e.getDest().exists() && !override) {
                            if(!interOp) mm.destExists(sender);
                            else mm.destExistsPiped(sender, e.getDest());
                            return;
                        }
                        
                        if(piped) {
                            pipes[c] = () -> {
                                p.compress(sender, e.getSrc(), e.getDest(), log, interOp);
                                e.getSrc().delete();
                            };
                        } else {
                            pipes[c] = () -> {
                                p.compress(sender, e.getSrc(), e.getDest(), log, interOp);
                            };
                        }
                        piped = true;
                    } else {
                        mm.invalidSourceForDest(sender, p.canCompressFrom(), p.canCompressTo());
                        return;
                    }
                }
            }
            // If we can't process this phase, cancel the operation.
            if(pipes[c] == null) {
                mm.invalidCompressionExtension(sender);
                return;
            }
            c++;
        }
        
        task = () -> {
            for(Runnable r : pipes) {
                r.run();
            }
        };

        int result = ZServicer.getInstance().submit(task);
        if (result == 0)
            mm.addToQueue(sender, ZServicer.getInstance().getSize());
        else if (result == 1)
            mm.queueFull(sender, ZServicer.getInstance().getMaxQueueSize());
        else if (result == 2)
            mm.executorTerminated(sender, ZTask.COMPRESS);
    }

    public static List<String> supportedExtensions() {
        if (SUPPORTED == null) {
            SUPPORTED = new ArrayList<String>();
            for (final TypeProvider p : TypeProvider.getProviders()) {
                SUPPORTED.addAll(p.canCompressTo());
            }
        }
        return SUPPORTED;
    }

}
