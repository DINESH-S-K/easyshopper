import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {
  const inventoryDescriptionElement = document.getElementById('inventoryDescription');
  const productTableBody = document.getElementById('productTableBody');
  const addProductButton = document.getElementById('addProductButton');
  const backToInventoryButton = document.getElementById('backToInventoryButton');

  AuthUtility.userInfo().then(data => {
    console.log(data);
    if (!data && data.roleId !== 1 && data.roleId!==2) {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

  const urlParams = new URLSearchParams(window.location.search);
  const inventoryId = urlParams.get('inventoryId');

  function fetchInventoryDetails(inventoryId) {
    fetch(`http://localhost:8080/easyshopper_server2/viewInventory?inventoryId=${inventoryId}`, {
      method: 'GET',
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
        inventoryDescriptionElement.textContent = data.description;
        console.log(data.description);
        displayProducts(data.productList);
      })
      .catch(error => {
        console.error('Error:', error);
        inventoryDescriptionElement.textContent = 'An error occurred while fetching inventory details.';
      });
  }

  // Function to display the products in the table
  function displayProducts(products) {
    if (products.length === 0) {
      productTableBody.innerHTML = '<tr><td colspan="6">No products found in this inventory.</td></tr>';
      return;
    }

    productTableBody.innerHTML = '';

    products.forEach(product => {
      const row = document.createElement('tr');

      const nameCell = createTableCell(product.productName);
      row.appendChild(nameCell);

      const descriptionCell = createTableCell(product.description);
      row.appendChild(descriptionCell);

      const unitPriceCell = createTableCell(product.unitPrice.toFixed(2));
      row.appendChild(unitPriceCell);

      const quantityInStockCell = createTableCell(product.quantityInStock);
      row.appendChild(quantityInStockCell);

      const actionsCell = document.createElement('td');

      const editButton = createButton("bi bi-pen", () => {
        onEditProduct(row, product);
      },'edit-button');
      actionsCell.appendChild(editButton);

      row.appendChild(actionsCell);

      productTableBody.appendChild(row);
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
    button.classList.add(className);
    button.addEventListener('click', onClick);
    return button;

  }

  function createEditableInput(content) {
    const input = document.createElement('input');
    input.type = 'text';
    input.value = content;
    return input;
  }

  function onEditProduct(row, product) {
    const nameInput = createEditableInput(product.productName);
    row.cells[0].innerHTML = '';
    row.cells[0].appendChild(nameInput);

    const descriptionInput = createEditableInput(product.description);
    row.cells[1].innerHTML = '';
    row.cells[1].appendChild(descriptionInput);

    const unitPriceInput = createEditableInput(product.unitPrice.toFixed(2));
    row.cells[2].innerHTML = '';
    row.cells[2].appendChild(unitPriceInput);

    const quantityInStockInput = createEditableInput(product.quantityInStock);
    row.cells[3].innerHTML = '';
    row.cells[3].appendChild(quantityInStockInput);

    const saveButton = createButton('bi bi-check-lg', () => {
      onSaveProduct(row, product, {
        name: nameInput.value,
        description: descriptionInput.value,
        unitPrice: parseFloat(unitPriceInput.value),
        quantityInStock: parseInt(quantityInStockInput.value),
      });
    },'update-button');

    const cancelButton = createButton('bi bi-x-lg', () => {
      onCancelEditProduct(row, product);
    },'cancel-button');

    const deleteButton = createButton('bi bi-trash-fill', () => {
      onDeleteProduct(product.productId);
    },'delete-button');

    row.cells[4].innerHTML = '';
    row.cells[4].appendChild(saveButton);
    row.cells[4].appendChild(cancelButton);
    row.cells[4].appendChild(deleteButton);

    saveButton.style.marginRight = '12px'; 
    cancelButton.style.marginRight = '12px'; 
  }


  function onSaveProduct(row, product, updatedProduct) {
    console.log(`Updating product with ID: ${product.id}`);
    const apiUrl = `http://localhost:8080/easyshopper_server2/updateProduct?productId=${product.productId}&productName=${encodeURIComponent(updatedProduct.name)}&description=${encodeURIComponent(updatedProduct.description)}&unitPrice=${encodeURIComponent(updatedProduct.unitPrice)}&quantityInStock=${encodeURIComponent(updatedProduct.quantityInStock)}`;

    fetch(apiUrl, {
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
        console.log('Product updated successfully:', data);
        alert('Product updated successfully.');
        window.location.reload();
      })
      .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while updating the product.');
      });
  }

  function onCancelEditProduct(row, product) {
    window.location.reload();
  }

  function onDeleteProduct(productId) {
    console.log(`Removing product with ID: ${productId}`);
    fetch(`http://localhost:8080/easyshopper_server2/removeProduct?productId=${productId}`, {
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
        alert('An error occurred while removing the manager. Please try again later.');
      });
  }

  function onAddProduct() {
    console.log('Adding a new product');
    window.location.href = `addproduct.html?inventoryId=${inventoryId}`;
  }

  function onBackToInventory() {
    console.log('Going back to inventory');
    window.location.href = `inventoryoperations.html`;
  }

  fetchInventoryDetails(inventoryId);

  addProductButton.addEventListener('click', onAddProduct);

  backToInventoryButton.addEventListener('click', onBackToInventory);
});
