import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
    if (!data) {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});
logout();

function logout() {
    fetch(`http://localhost:8080/easyshopper_server2/logout`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
        .then(response => {
            if (response.ok) {
                console.log('Logout successfully.');
                alert('Logout successfully.')
                window.location.href = 'login.html';
            } else {
                throw new Error('Failed to logout.');
            }
        })
        .then(data => {
            document.cookie = 'easyshopper=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while removing the manager. Please try again later.');
        });
}