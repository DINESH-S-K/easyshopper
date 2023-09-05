import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {
  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (!data && data.roleId !== 1) {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

  const urlParams = new URLSearchParams(window.location.search);
  const managerId  = urlParams.get('managerId')

  const managerIdElement = document.getElementById('managerId');
  const usernameElement = document.getElementById('username');
  const emailElement = document.getElementById('email');
  const mobileNoElement = document.getElementById('mobileNo');
  const createdByElement = document.getElementById('createdBy');
  const updatedByElement = document.getElementById('updatedBy');
  const createdTimeElement = document.getElementById('createdTime');
  const updatedTimeElement = document.getElementById('updatedTime');

  fetchManagerDetails(managerId);

  // Function to fetch the manager details from the API based on the manager ID
  function fetchManagerDetails(managerId) {
    fetch(`http://localhost:8080/easyshopper_server2/getManager?managerId=${managerId}`, {
      method: 'GET',
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
        managerIdElement.textContent = data.id;
        usernameElement.textContent = data.username;
        emailElement.textContent = data.email;
        mobileNoElement.textContent = data.mobileNo;
        createdByElement.textContent = data.createdBy;
        updatedByElement.textContent = data.updatedBy;
        createdTimeElement.textContent = data.createdTime;
        updatedTimeElement.textContent = data.updatedTime;
      })
      .catch(error => {
        console.error('Error:', error);
        managerIdElement.textContent = 'Error occurred while fetching manager details.';
      });
  }

});

document.getElementById('back-button').addEventListener('click', function () {
  window.location.href = `manageroperations.html`;
});