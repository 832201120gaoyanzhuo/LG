const httpGet = (url, data,) => {
    $.ajax({
        url: url,
        type: 'GET',
        data: data,
        success: res => {

        },
        error: err => {

        }
    })
}