import AuthUtility from "../authutility.js";

let username, id;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data) {
    if (data.roleId === 1) {
      id = data.id;
      username = data.username;
      document.getElementById('admin-panel').innerHTML = "";
      document.getElementById('admin-panel').innerHTML = "Hello " + username;
    } else {
      alert("Access denied. You are not authorized to access this page.");
      window.location.href = 'login.html';
    }
  } else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }

});

document.getElementById('changePasswordBtn').addEventListener('click', function () {
  window.location.href = 'changepassword.html'
});

document.getElementById('updateProfileBtn').addEventListener('click', function () {
  window.location.href = `updateprofile.html`;
});

document.getElementById('managerOperationsBtn').addEventListener('click', function () {
  window.location.href = `manageroperations.html`;
})

document.getElementById('inventoryOperationsBtn').addEventListener('click', function () {
  window.location.href = `inventoryoperations.html`;
})

document.getElementById('LogsBtn').addEventListener('click', function () {
  window.location.href = `logs.html`;
})

document.getElementById('logoutBtn').addEventListener('click', function () {
  window.location.href = `logout.html`;
});
