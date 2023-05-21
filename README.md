# Just Player Receiver

An app to play videos by URL on [Just Player](https://github.com/moneytoo/Player) via HTTP.

This app can be combined with [HTTP Shortcuts](https://github.com/Waboodoo/HTTP-Shortcuts) to get a chromecast-like experience, like this:

1. Run this app and note down the MDNS id shown in the toast (e.g `my-just-player.local`)
2. Create a regular HTTP shortcut, with the following parameters:
  - In base request section, add a new "URL" variable (check "encode URL" and "allow share")
  - Request method: `POST`
  - Request URL: `http://my-just-player.local/play?url={URL}` (use the "URL" variable created before)
3. Now you can share a video URL and select the HTTP shortcut

The shared URL will be sent via HTTP to this app, which will in turn start Just Player and play the URL
