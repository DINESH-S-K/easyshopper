import AuthUtility from "../authutility.js";


getUserInfo()
function getUserInfo() {
    AuthUtility.userInfo().then(data => {
        if (data) {
            if (data.roleId === 1) {
                window.location.href = `admin.html`;
            } else if (data.roleId === 2) {
                window.location.href = `manager.html`;
            } else if (data.roleId === 3) {
                window.location.href = `customer.html`;
            } else {
                console.error("Invalid roleId:", data.roleId);
            }
        }
    })
}

document.getElementById('loginForm').addEventListener('submit', function (event) {
    event.preventDefault();

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    fetch(`http://localhost:8080/easyshopper_server2/login?username=${username}&password=${password}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
        .then(
            response => {
                if (response.status == 400) {
                    alert('Invalid Username And Password');
                    throw new Error('Invalid Username And Password');
                }
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                alert('Login Successful');
                getUserInfo();
            })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('msg').innerText = 'An error occurred. Please try again.';
        });
});

document.getElementById('forgotPasswordBtn').addEventListener('click', function () {
    window.location.href = 'forgetpassword.html';
});

