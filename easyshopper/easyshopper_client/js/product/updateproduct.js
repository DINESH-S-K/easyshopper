const urlParams = new URLSearchParams(window.location.search);
const inventoryId = urlParams.get('inventoryId');
const productId = urlParams.get('productId');

document.addEventListener('DOMContentLoaded', function() {
    const updateProductForm = document.getElementById('updateProductForm');
    const backButton = document.getElementById('backButton');
    fetchProductData(productId);
  
    updateProductForm.addEventListener('submit', function(event) {
      event.preventDefault();
  
      const name = document.getElementById('name').value;
      const description = document.getElementById('description').value;
      const unitPrice = parseFloat(document.getElementById('unitPrice').value);
      const quantityInStock = parseInt(document.getElementById('quantityInStock').value);
  
      if (validateForm(name, description, unitPrice, quantityInStock)) {
        updateProduct(userId,productId, name, description, unitPrice, quantityInStock);
      }
    });
  
    backButton.addEventListener('click', function() {
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
  
  function updateProduct(userId,productId, name, description, unitPrice, quantityInStock) {
    // You can modify the API endpoint and request method as per your backend
    const apiUrl = `http://localhost:8080/server/updateProduct?id=${productId}&name=${encodeURIComponent(name)}&description=${encodeURIComponent(description)}&unitPrice=${encodeURIComponent(unitPrice)}&updatedBy=${encodeURIComponent(userId)}&quantityInStock=${encodeURIComponent(quantityInStock)}`;

    fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      console.log('Product updated successfully:', data);
      alert('Product updated successfully.');
      goBackAndReload();
    })
    .catch(error => {
      console.error('Error:', error);
      alert('An error occurred while updating the product.');
    });
  }
  
  function fetchProductData(productId) {
    // You can modify the API endpoint and request method as per your backend
    fetch(`http://localhost:8080/server/getProduct?id=${productId}`, {
      method: 'GET',
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
      console.log('Product Data:', data);
      // Populate the input fields with the received data
      document.getElementById('name').value = data.bean.name;
      document.getElementById('description').value = data.bean.description;
      document.getElementById('unitPrice').value = data.bean.unitPrice;
      document.getElementById('quantityInStock').value = data.bean.quantityInStock;
    })
    .catch(error => {
      console.error('Error:', error);
      alert('An error occurred while fetching product data.');
    });
  }
  
  function goBackAndReload() {
    window.location.href = `updateinventory.html?id=${userId}&inventoryId=${inventoryId}`;
    window.location.reload();
  }
  
  function goBack() {
    window.location.href = `updateinventory.html?id=${userId}&inventoryId=${inventoryId}`;
    window.history.reload();
  }
  