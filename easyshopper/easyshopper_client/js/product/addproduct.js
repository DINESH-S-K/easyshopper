import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {

  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (!data && data.roleId !== 1 && data.roleId!==2) {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

  const urlParams = new URLSearchParams(window.location.search);
  const inventoryId = urlParams.get('inventoryId');


  const addProductForm = document.getElementById('addProductForm');
  const backButton = document.getElementById('backButton');

  addProductForm.addEventListener('submit', function (event) {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const description = document.getElementById('description').value;
    const unitPrice = parseFloat(document.getElementById('unitPrice').value);
    const quantityInStock = parseInt(document.getElementById('quantityInStock').value);

    if (validateForm(name, description, unitPrice, quantityInStock)) {
      addProduct(inventoryId, name, description, unitPrice, quantityInStock);
    }
  });

  backButton.addEventListener('click', function () {
    goBack();
  });
});

function validateForm(name, description, unitPrice, quantityInStock) {
  if (!name || !description || isNaN(unitPrice) || isNaN(quantityInStock)) {
    alert('All fields are required.');
    return false;
  }
  return true;
}

function addProduct(inventoryId, name, description, unitPrice, quantityInStock) {
  // You can modify the API endpoint and request method as per your backend
  const apiUrl = 'http://localhost:8080/easyshopper_server2/addProduct';
  const queryParams = `?inventoryId=${inventoryId}&productName=${encodeURIComponent(name)}&description=${encodeURIComponent(description)}&unitPrice=${encodeURIComponent(unitPrice)}&quantityInStock=${encodeURIComponent(quantityInStock)}`;

  fetch(apiUrl + queryParams, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials:'include'
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      console.log('Product added successfully:', data);
      alert('Product added successfully.');
      window.location.href = `updateinventory.html?inventoryId=${inventoryId}`
    })
    .catch(error => {
      console.error('Error:', error);
      alert('An error occurred while adding the product.');
    });
}

function goBack() {
  window.history.back();
}
