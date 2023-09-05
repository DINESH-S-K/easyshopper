document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // Get the input values
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmNewPassword = document.getElementById('confirm-new-password').value;

    if (newPassword !== confirmNewPassword) {
        document.getElementById('msg').innerText = "passwords don't match.";
        return;
    }

    const url = `http://localhost:8080/easyshopper_server2/forgetAndChangePassword?username=${username}&email=${email}&newPassword=${newPassword}`;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('msg').innerText = data.msg;
        alert("Password changed");
        window.location.href='login.html';
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('msg').innerText = 'An error occurred. Please try again.';
    });
});
