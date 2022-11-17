# Offline Zip Browser

This project is meant so that I can play interactive fiction games, but it's also good for any similar project. You can even play my LizardUp game if you download the source as a zip and open it.

## Use

1. Use "Open with..." from your file app on a `.zip` file or standalone `.html` file, and select "Offline Zip Browser".
1. Enter a name for it (you cannot change this yet)
    - The app will attempt to guess if .zip or .html but may get it wrong
		so it's good to specify .zip or .html in the name too.
1. The file will be saved to app's storage and now any time you open the app
you will have access to this file. LocalStorage will persist across sessions.
Which means most interactive fiction games with save states will work.

Note: if you use termux + curl to download files, you will have to set the
`allow-external-apps` property must be set to `true` in `~/.termux/termux.properties` in termux.

## Deep Links

Deep links to link into the app. Because the app is intentionally not communicating with servers or other apps, deep links do not work inside the app, so you can only link in from another app.

Debug builds of the app use a different deep link hostname (add .debug to the end of the hostname)

- [Delete a file](app://foss.zip.offline.browser.offlinezipbrowser/action/delete-file)
- [Rename a file](app://foss.zip.offline.browser.offlinezipbrowser/action/rename-file)
- Deep link to play a game app://foss.zip.offline.browser.offlinezipbrowser/play/Your_game_here.html

### `RENAME_FILE`

rename a file

### `DELETE_FILE`

delete a file

