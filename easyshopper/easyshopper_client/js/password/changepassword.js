import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (!data) {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});


document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('old-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmNewPassword = document.getElementById('confirm-new-password').value;

    if (newPassword !== confirmNewPassword) {
        document.getElementById('msg').innerText = "New passwords don't match.";
        return;
    }

    const registerUrl = `http://localhost:8080/easyshopper_server2/changePassword?username=${username}&password=${password}&newPassword=${newPassword}`;

    fetch(registerUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('msg').innerText = data.msg;
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('msg').innerText = 'An error occurred. Please try again.';
    });
});
