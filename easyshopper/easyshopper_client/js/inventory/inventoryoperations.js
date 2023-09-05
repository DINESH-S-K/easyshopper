import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {

  let id;
  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (data && (data.roleId == 1 || data.roleId==2)) {
      id = data.roleId;
    }else{
      alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

  const inventoryTableBody = document.getElementById('inventoryBody');
  const addInventoryButton = document.getElementById('addInventoryButton');

  fetchInventory();

  // Function to fetch the inventory data from the API
  function fetchInventory() {
    fetch('http://localhost:8080/easyshopper_server2/viewInventories', {
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
        displayInventory(data);
      })
      .catch(error => {
        console.error('Error:', error);
        inventoryTableBody.innerHTML = 'An error occurred while fetching the inventory data.';
      });
  }

  // Function to display the inventory data in the table
  function displayInventory(inventoryList) {
    if (inventoryList.length === 0) {
      inventoryTableBody.innerHTML = '<tr><td colspan="5">No inventory items found.</td></tr>';
      return;
    }

    inventoryTableBody.innerHTML = '';

    inventoryList.forEach(item => {
      const row = document.createElement('tr');

      const descriptionCell = createTableCell(item.description);
      row.appendChild(descriptionCell);

      const actionsCell = document.createElement('td');

      const updateButton = createButton('fa fa-pencil', () => {
        onUpdateInventory(item.id);
      }, 'update');
      actionsCell.appendChild(updateButton);

      const removeButton = createButton('fa fa-remove', () => {
        onRemoveInventory(item.id);
      }, 'remove');
      actionsCell.appendChild(removeButton);

      updateButton.style.marginRight = '12px'; 

      row.appendChild(actionsCell);

      inventoryTableBody.appendChild(row);
    });
  }

  // Helper function to create a table cell
  function createTableCell(content) {
    const cell = document.createElement('td');
    cell.textContent = content;
    return cell;
  }

  // Helper function to create a button
  function createButton(iconClass, onClick, className) {
    const button = document.createElement('button');
    const iconElement = document.createElement('i');
    iconElement.className = iconClass;
    button.appendChild(iconElement);
    button.addEventListener('click', onClick);
    button.classList.add(className);
    return button;

}

  // Function to handle the "Update" button click
  function onUpdateInventory(itemId) {
    console.log(`Updating inventory item with ID: ${itemId}`);
    window.location.href = `updateinventory.html?inventoryId=${itemId}`;
  }

  // Function to handle the "Remove" button click
  function onRemoveInventory(itemId) {
    console.log(`Removing inventory item with ID: ${itemId}`);
    fetch(`http://localhost:8080/easyshopper_server2/removeInventory?inventoryId=${itemId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials:'include'
    })
      .then(response => {
        if (response.ok) {
          console.log('Inventory removed successfully.');
          window.location.reload();
        } else {
          throw new Error('Failed to remove manager.');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        // Handle the error, e.g., display an error message
        alert('An error occurred while removing the manager. Please try again later.');
      });
  }


  addInventoryButton.addEventListener('click', onAddInventory);

  function onAddInventory() {
    console.log("Adding a new inventory item");
    window.location.href = `addinventory.html`;
  }

  const backButton = document.getElementById('backButton');

  backButton.addEventListener('click', function () {
    console.log(id);
    goBack();
  });

  function goBack() {
    if(id ==1){
    window.location.href = 'admin.html'
    }else{
      window.location.href = 'manager.html';
    }
  }
});
