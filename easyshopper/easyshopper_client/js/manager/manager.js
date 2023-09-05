import AuthUtility from "../authutility.js";

document.getElementById('changePasswordBtn').addEventListener('click', onChangePassword);
document.getElementById('updateProfileBtn').addEventListener('click', onUpdateProfile);
document.getElementById('inventoryOperationsBtn').addEventListener('click', onInventoryOperations);
document.getElementById('logoutBtn').addEventListener('click', onLogout);

let id,username;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data.roleId === 2) {
    id = data.id;
    username = data.username;
    document.getElementById('admin-panel').innerHTML = "";
    document.getElementById('admin-panel').innerHTML = "Hello " + username;
  } else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

// Function for Change Password button click
function onChangePassword() {
  console.log('Change Password clicked.');
   window.location.href = 'changepassword.html'
}

// Function for Update Profile button click
function onUpdateProfile() {
  console.log('Update Profile clicked.');
  window.location.href = `updateprofile.html`;
}

// Function for Inventory Operations button click
function onInventoryOperations() {
  console.log('Inventory Operations clicked.');
  window.location.href = `inventoryoperations.html`;
}

// Function for Logout button click
function onLogout() {
  console.log('Logout clicked.');
   window.location.href = `logout.html?id=${id}`;
}
