/**
* Download a file to disk using a specified buffer size.
* functions as a js to kotlin bridge
* @param url string
* @param buffer_size int
* @param fileId int
*/
(function(url, buffer_size, fileId) {
    const request = new XMLHttpRequest();
    const file_reader = new FileReader();
    request.open("GET", url, true);
    request.responseType = "blob";
    request.onload = download;
    request.send();
    let bytesRead = 0;
    function sliceRead(event) {
        const response = request.response;
        bytesRead += event.loaded;
        Android.writeToDisk(file_reader.result, fileId)
        if (bytesRead < response.size) {
            download();
        } else {
            close();
        }
    }
    function close(event) {
        Android.close(fileId)
    }
    function download() {
        const response = request.response;
        file_reader.onload = sliceRead;
        file_reader.onabort = close;
        file_reader.onerror = close;
        // possibly try to get arraybuffer out of the blob
        file_reader.readAsBinaryString(response.slice(bytesRead, bytesRead + buffer_size));
    }
    // intentionally no new line at end of file. file called by concatenation
})