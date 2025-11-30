function sendRequest(method, endpoint, data, responseContainerId) {
    const responseContainer = document.getElementById(responseContainerId);
    
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        }
    };

    if (data && method !== 'GET') {
        options.body = JSON.stringify(data);
    }

    // Show loading state
    responseContainer.innerHTML = '<div class="response-status success">Loading...</div>';
    responseContainer.classList.add('show');

    fetch(`http://localhost:8081${endpoint}`, options)
        .then(response => {
            const statusClass = response.ok ? 'success' : 'error';
            const statusText = `${response.status} ${response.statusText}`;
            
            return response.json().then(body => {
                showResponse(responseContainer, statusClass, statusText, body);
            }).catch(() => {
                showResponse(responseContainer, statusClass, statusText, 'No response body');
            });
        })
        .catch(error => {
            showResponse(responseContainer, 'error', 'Network Error', {
                message: error.message,
                hint: 'Make sure the backend is running on http://localhost:8081'
            });
        });
}

function showResponse(container, statusClass, statusText, data) {
    container.innerHTML = `
        <div class="response-status ${statusClass}">${statusText}</div>
        <div class="response-body">
            <pre>${typeof data === 'string' ? data : JSON.stringify(data, null, 2)}</pre>
        </div>
    `;
    container.classList.add('show');
}
