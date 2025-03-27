// client.js

// Use the backend API URL from environment variables
const API_URL = process.env.REACT_APP_API_URL || '';

const checkStatus = response => {
    // console.log(response)
    if (response.ok){
        return response;
    }
    const error = new Error(response.statusText);
    error.response = response;
    return Promise.reject(error);
}

// Helper to prepend the API URL to the path
const buildUrl = (path) => `${API_URL}${path}`;

export const post = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
    };
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}

export const getWithJwt = (path) => {
    const requestOptions = {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        }
    };
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}

export const postWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}

export const putWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'PUT',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}

export const deleteWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'DELETE',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}

export const postImageWithJwt = (path, imageData) => {
    const formData = new FormData();
    formData.append("image", imageData);
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            // Note: Let the browser set Content-Type for FormData.
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: formData
    };
    console.log(requestOptions);
    return fetch(buildUrl(path), requestOptions)
        .then(response => checkStatus(response));
}
