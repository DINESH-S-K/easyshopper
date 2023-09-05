import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {

  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (!data && data.roleId !== 1 && data.roleId!==2) {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

  const addInventoryForm = document.getElementById('addInventoryForm');
  const descriptionInput = document.getElementById('description');

  function onAddInventorySubmit(event) {
    event.preventDefault();

    // Get the values from the form
    const description = descriptionInput.value;

    // Validate the input
    if (!description) {
      descriptionError.textContent = 'Please enter a description.';
      return;
    }

    // Call the function to make the POST call and add the inventory item
    addInventory(description);
  }

  // Function to make the POST call and add the inventory item
  function addInventory(description) {

    fetch(`http://localhost:8080/easyshopper_server2/addInventory?description=${description}`, {
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
        console.log('Inventory Item Added:', data);
        alert('New Inventory added successfully.');
        window.location.href = `inventoryoperations.html`;
      })
      .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while adding the inventory item.');
      });
  }

  // Add event listener for form submission
  addInventoryForm.addEventListener('submit', onAddInventorySubmit);
});
