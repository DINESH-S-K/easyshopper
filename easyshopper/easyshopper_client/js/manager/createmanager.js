import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function (){

  let id;
  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (data && data.roleId === 1) {
      id = data.id
      setRoleRadioButtons(2)
    } else {
      alert("Access denied. You are not authorized to access this page.");
      window.location.href = 'login.html';
    }
  });

  function setRoleRadioButtons(roleId) {
    const roleRadios = document.getElementsByName('role');
    roleRadios.forEach(roleRadio => {
      roleRadio.checked = roleRadio.value === roleId.toString();
      roleRadio.disabled = roleRadio.value !== roleId.toString();
    });
  }
  

  const form = document.getElementById('registration-form');
  form.addEventListener('submit', function (event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const mobileNo = document.getElementById('mobileNo').value;
    const password = document.getElementById('password').value;
    if (validateForm(username, email, mobileNo, password)) {
      registerUser();
    }
  });


  function validateForm(username, email, mobileNo, password) {
    if (!username || !email || !mobileNo || !password) {
      alert('All fields are required.');
      return false;
    }
    return true;
  }

  function registerUser() {
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const mobileNo = document.getElementById('mobileNo').value;
    const password = document.getElementById('password').value;
    const roleRadios = document.getElementsByName('role');

    let selectedRole;
    for (const roleRadio of roleRadios) {
      if (roleRadio.checked) {
        selectedRole = roleRadio.value;
        break;
      }
    }
    const url = `http://localhost:8080/easyshopper_server2/createManager?username=${username}&email=${email}&mobileNo=${mobileNo}&password=${password}&roleId=${selectedRole}&createdBy=${id}&updatedBy=${id}`;

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(response => {
        if (response.ok) {
          showMessageAndNavigate();
        } else {
          alert('Registration failed. Please try again.');
        }
      })
      .catch(error => {
        alert('An error occurred during registration. Please try again later.');
        console.error('API call error:', error);
      });
  }

  function showMessageAndNavigate() {
    alert('Registration successful! Now navigate to Admin Page');
    window.location.href = `admin.html`;
  }
});



