document.addEventListener('DOMContentLoaded', function () {

  document.getElementById('registration-form').addEventListener('submit', function (event) {

    event.preventDefault();
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const mobileNo = document.getElementById('mobileNo').value;
    const password = document.getElementById('password').value;
    const roleRadios = document.getElementsByName('role');
    if (validateForm(username, email, mobileNo, password, roleRadios)) {
      registerUser(username, email, mobileNo, password, roleRadios);
    }
  });

  function validateForm(username, email, mobileNo, password, roleRadios) {
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

    if (!username || !email || !mobileNo || !password) {
      alert('All fields are required.');
      return false;
    }
    return true;
  }

  function registerUser(username, email, mobileNo, password, roleRadios) {
    for (const roleRadio of roleRadios) {
      if (roleRadio.checked) {
        selectedRole = roleRadio.value;
        break;
      }
    }
    const url = `http://localhost:8080/easyshopper_server2/register?username=${username}&email=${email}&mobileNo=${mobileNo}&password=${password}&roleId=${selectedRole}`;

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        if (response.status == 201) {
          showMessageAndNavigate();
        }else if(response.status == 409){
          alert('Already username and password taken.')
        }else if(response.status == 500){
          alert('Internal Server Error')
        }else {
          alert('Registration failed. Bad Request');
          throw new Error('Network response was not ok');
        }
      })
      .catch(error => {
        alert('An error occurred during registration. Please try again later.');
        console.error('API call error:', error);
      });
  }

  function showMessageAndNavigate() {
    alert('Registration successful! Now navigate to Login Page');
    window.location.href = 'index.html';
  }
});

