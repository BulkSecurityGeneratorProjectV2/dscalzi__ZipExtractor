![# Header](http://i.imgur.com/NNwBkWb.png)

[![](http://ci.aventiumsoftworks.com/jenkins/job/ZipExtractor/badge/icon)](http://ci.aventiumsoftworks.com/jenkins/job/ZipExtractor/) [![](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/dscalzi/ZipExtractor/blob/master/LICENSE) ![](https://img.shields.io/badge/Spigot-1.8--1.12-orange.svg) ![](https://img.shields.io/badge/Java-8+-ec2025.svg) [![](https://discordapp.com/api/guilds/211524927831015424/widget.png)](https://discordapp.com/invite/Fcrh6PT)

ZipExtractor is an administrative utility plugin allowing the compression/extraction of archived files through minecraft command. This plugin is extremely useful for dealing with archives over FTP, which does not provide support for neither compression nor extraction. While using this plugin please note that **there is no undo button**. Overridden files **cannot** be recovered.

The source and destination file paths are saved inside of the config.yml. This means that only one can be set at a time. If you edit these values directly in the config.yml you must reload the plugin for the new values to take effect.

---

# Feature List

* Extraction of **ZIP**, **RAR**, and **JAR** archives.
* Compression of any file into the **ZIP** format.
* Queueable operations if you have many extractions/compressions to perform.
* Configurable [Thread Pool Executor][thread_pools] allowing you to set a maximum queue size and maximum number of threads to run at once. Incase of an emergency the Thread Pool can be shutdown at anytime.
* Option to be warned if an extraction/compression would result in files being overriden.
    * If enabled, users will require an additional permission in order to proceed with the process.
    * For extractions, you can view every file which would be overriden prior to proceeding with the process.
* Metrics by [bStats][bStats]

You can find more extensive details on the [wiki][wiki].

***

# Contributing

If you would like to contribute to this project, feel free to submit a pull request. The project does not use a specific code style, but please keep to the conventions used throughout the code.

To build this project you will need maven. Clone this repo and run `mvn clean install`.

Since the main purpose of this plugin deals with archive manipulation, the plugin uses a provider system so that new formats can be easily supported. If you need support for a specific file extension you can create an issue and request it or submit a pull request which adds the provider. The *TypeProvider* class is documented in the code and implementations already exist if you need examples. A reference to each provider is saved in the *ZipExtractor* class.

***

# Links
* [Spigot Resource Page][spigot]
* [Dev Bukkit Page][devbukkit]
* [Suggest Features or Report Bugs][issues]

[thread_pools]: http://tutorials.jenkov.com/java-util-concurrent/threadpoolexecutor.html "Thread Pool Information"
[bStats]: https://bstats.org/plugin/bukkit/ZipExtractor "bStats page"
[wiki]: https://github.com/dscalzi/ZipExtractor/wiki "Wiki page"
[spigot]: https://www.spigotmc.org/resources/zipextractor.43482/ "Spigot"
[devbukkit]: https://dev.bukkit.org/projects/zipextractor "DevBukkit"
[issues]: https://github.com/dscalzi/ZipExtractor/issues "Issue Tracker"