const request = (options) => {
    const headers = new Headers({
        "Content-Type": "application/json",
    });

    const defaults = { headers: headers };
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options).then((response) =>
        response.json().then((json) => {
            if (!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function interfaceRequest() {
    return request({
        url: "http://localhost:31513/api/interface",
        method: "GET",
    });
}

export function solutionRequest(data) {
    return request({
        url: "http://localhost:31513/api/input",
        method: "POST",
        body: JSON.stringify(data)
    });
}