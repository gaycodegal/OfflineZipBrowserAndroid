/**
* Download a file to disk using a specified buffer size.
* functions as a js to kotlin bridge
* @param url string
* @param buffer_size int
* @param fileId int
*/
(function(url, buffer_size, fileId) {
    const request = new XMLHttpRequest();
    request.open("GET", url, true);
    request.responseType = "blob";
    request.onload = download;
    request.send();
    let response = null;
    let size = null;
    let bytesRead = 0;
    // windows-1251, which is a 1 byte per character scheme.
    // importantly it has every single possible 8 bit value
    // defined.
    //
    // java is very inefficient at converting typed arrays
    // from javascript to java, so this is our best workaround
    // learn more at
    // https://en.wikipedia.org/wiki/Windows-1251
    // https://stackoverflow.com/questions/27034897/is-there-a-way-to-pass-an-arraybuffer-from-javascript-to-java-on-android
    //
    // also unlike what the aforementioned link claims, 1251 is
    // actually supported, see:
    // https://developer.mozilla.org/en-US/docs/Web/API/TextDecoder/encoding
    let decoder = new TextDecoder("windows-1251");
    function sliceRead(event) {
        bytesRead += event.loaded;
	
        Android.writeToDisk(decoder.decode(this.result), fileId);
        if (bytesRead < size) {
            download();
        } else {
            close();
        }
    }
    function close(event) {
        Android.close(fileId)
    }
    function download() {
	response = response ?? request.response;
	size = size ?? response.size;
	const file_reader = new FileReader();
        file_reader.onload = sliceRead.bind(file_reader);
        file_reader.onabort = close;
        file_reader.onerror = close;
        // possibly try to get arraybuffer out of the blob
	// readAsBinaryString deprecated
        file_reader.readAsArrayBuffer(response.slice(bytesRead, bytesRead + buffer_size));
    }
    // intentionally no new line at end of file. file called by concatenation
})
