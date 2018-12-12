# Snake
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
![IRC: #shard @ esper](https://img.shields.io/badge/irc-%23shard%20%40%20irc.esper.net-ff69b4.svg)

This repository contains the code of Snake, the general Paper server management utility and heart of the [Shard](https://github.com/ShardPowered) ecosystem.

I've tried to make this configurable, however a lot of work can be put into making this thing more useful for the general public.

## A little bit about dependencies

Snake is build in top of [Easy](https://github.com/ShardPowered/easy), a Paper plugin framework. Snake does not depend on anything else, however other Shard plugins may have their own dependencies, for example ProtocolLib.

## Configuration

Snake generates its config files to `plugins/Snake/`. It uses [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) as a configuration format.

Some of the features (eg. ranks) must be configured in-game using commands.

You may give yourself admin permissions by running the command `setrank <username> ADMIN` (assuming you have not changed the admin rank)

## Database

Snake stores data on a MongoDB server. Using a MongoDB Replica Set is required due to WatchStreams requiring to use a replica set.

## License

- [MIT](LICENSE).
