document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const adminId  = urlParams.get('adminId');
    const managerId = urlParams.get('id');
    fetchUserData(managerId);

    document.getElementById('back-button').addEventListener('click', function () {
        window.location.href = `manageroperations.html?id=${adminId}`;
    });

    document.getElementById('update-profile-form').addEventListener('submit', function (event) {
        event.preventDefault();
        const updatedUsername = document.getElementById('username').value;
        const updatedEmail = document.getElementById('email').value;
        const updatedMobileNo = document.getElementById('mobileNo').value;
        const roleRadios = document.getElementsByName('role');
        if (validateForm(updatedUsername, updatedEmail, updatedMobileNo, roleRadios)) {
            updateUser(adminId,managerId, updatedUsername, updatedEmail, updatedMobileNo, roleRadios);
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

function updateUser(userId,managerId,username, email, mobileNo, roleRadios) {
    for (const roleRadio of roleRadios) {
        if (roleRadio.checked) {
            selectedRole = roleRadio.value;
            break;
        }
    }
    fetch(`http://localhost:8080/server/updateManager?id=${managerId}&username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}&mobileNo=${encodeURIComponent(mobileNo)}&roleId=${encodeURIComponent(selectedRole)}&updatedBy=${encodeURIComponent(userId)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
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
        })
        .catch(error => {
            console.error('Error:', error);

        });
}



// Function to fetch user data based on the 'id'
function fetchUserData(userId) {
    fetch(`http://localhost:8080/server/getUser?id=${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('User Data:', data);
            // Populate the input fields with the received data
            document.getElementById('username').value = data.username;
            document.getElementById('email').value = data.email;
            document.getElementById('mobileNo').value = data.mobileNo;
            populateRoleRadioButtons(data.roleId);
        })
        .catch(error => {
            console.error('Error:', error);
            // Handle the error
        });
}

function populateRoleRadioButtons(roleId) {
    const roleRadios = document.getElementsByName('role');
    for (const roleRadio of roleRadios) {
        if (roleRadio.value === roleId.toString()) {
            roleRadio.checked = true;
            break;
        }
    }
}

