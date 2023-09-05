import AuthUtility from "../authutility.js";
let id;
document.addEventListener('DOMContentLoaded', function () {

    AuthUtility.userInfo().then(data => {
        if (data) {
            console.log(data);
            id = data.id;
            document.getElementById('username').value = data.username;
            document.getElementById('email').value = data.email;
            document.getElementById('mobileNo').value = data.mobileNo;
            populateRoleRadioButtons(data.roleId);
        } else {
            alert("Access denied. You are not authorized to access this page.");
            window.location.href = 'login.html';
        }
    });
    function populateRoleRadioButtons(roleId) {
        const roleRadios = document.getElementsByName('role');
        for (const roleRadio of roleRadios) {
            if (roleRadio.value === roleId.toString()) {
                roleRadio.checked = true;
            } else {
                roleRadio.disabled = true;
            }
        }
    }

    document.getElementById('update-profile-form').addEventListener('submit', function (event) {
        event.preventDefault();
        const updatedUsername = document.getElementById('username').value;
        const updatedEmail = document.getElementById('email').value;
        const updatedMobileNo = document.getElementById('mobileNo').value;
        const roleRadios = document.getElementsByName('role');
        if (validateForm(updatedUsername, updatedEmail, updatedMobileNo, roleRadios)) {
            updateUser(id, updatedUsername, updatedEmail, updatedMobileNo, roleRadios);
        }
    });
});


function validateForm(username, email, mobileNo, roleRadios) {
    let selectedRole;
    for (const roleRadio of roleRadios) {
        if (roleRadio.checked) {
            selectedRole = roleRadio.value;
            break;
        }
    }
    if (!selectedRole) {
        alert('Please select a role.');
        return;
    }

    if (!username || !email || !mobileNo) {
        alert('All fields are required.');
        return false;
    }
    return true;
}

function updateUser(id, username, email, mobileNo, roleRadios) {
    let selectedRole;
    for (const roleRadio of roleRadios) {
        if (roleRadio.checked) {
            selectedRole = roleRadio.value;
            break;
        }
    }
    fetch(`http://localhost:8080/easyshopper_server2/updateProfile?username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}&mobileNo=${encodeURIComponent(mobileNo)}&updatedBy=${encodeURIComponent(id)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
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
            console.log('Update Profile Success:', data);
            alert("Updated Successfully")
            window.location.href = 'admin.html';
        })
        .catch(error => {
            console.error('Error:', error);

        });
}



