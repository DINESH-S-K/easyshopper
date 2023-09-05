import AuthUtility from "../authutility.js";

document.addEventListener('DOMContentLoaded', function () {

    AuthUtility.userInfo().then(data => {
        console.log(data);
        if (!data && data.roleId !== 1) {
            alert("Access denied. You are not authorized to access this page.");
            window.location.href = 'login.html';
        }
    });

    fetchManagers();
    function fetchManagers() {
        fetch('http://localhost:8080/easyshopper_server2/getManagers', {
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
                displayManagers(data);
            })
            .catch(error => {
                console.error('Error:', error);
                managerListContainer.innerHTML = 'An error occurred while fetching the manager list.';
            });
    }


    const managerTableBody = document.getElementById('managerTableBody');

    function displayManagers(managers) {
        if (managers.length === 0) {
            managerTableBody.innerHTML = '<tr><td colspan="6">No Managers found</td></tr>';
            return;
        }

        managerTableBody.innerHTML = '';

        managers.forEach(manager => {
            const managerRow = document.createElement('tr');

            managerRow.classList.add('manager-row');

            const usernameElement = createTableCell(manager.username);
            managerRow.appendChild(usernameElement);

            const emailElement = createTableCell(manager.email);
            managerRow.appendChild(emailElement);

            const mobileNoElement = createTableCell(manager.mobileNo);
            managerRow.appendChild(mobileNoElement);

            const actionsCell = document.createElement('td');
            actionsCell.classList.add('actions-cell');

            const viewButton = createButton('bi bi-eye', () => {
                onViewManager(manager.id);
            }, 'view-button');
            actionsCell.appendChild(viewButton);

            const editButton = createButton("bi bi-pen", () => {
                onEditManager(managerRow, manager);
            }, 'edit-button');
            actionsCell.appendChild(editButton);

            const removeButton = createButton('bi bi-trash-fill', () => {
                onRemoveManager(manager.id);
            }, 'remove-button');
            actionsCell.appendChild(removeButton);

            viewButton.style.marginRight = '12px';
            editButton.style.marginRight = '12px';

            managerRow.appendChild(actionsCell);

            managerTableBody.appendChild(managerRow);
        });
    }

    function createTableCell(content) {
        const cell = document.createElement('td');
        cell.innerHTML = content;
        return cell;
    }

    function createButton(iconClass, onClick, className) {
        const button = document.createElement('button');
        const iconElement = document.createElement('i');
        iconElement.className = iconClass;
        button.appendChild(iconElement);
        button.addEventListener('click', onClick);
        button.classList.add(className);
        return button;

    }

    function createEditableInput(content) {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = content;
        return input;
    }

    function onEditManager(row, manager) {

        const usernameInput = createEditableInput(manager.username);
        row.cells[0].innerHTML = '';
        row.cells[0].appendChild(usernameInput);

        const emailInput = createEditableInput(manager.email);
        row.cells[1].innerHTML = '';
        row.cells[1].appendChild(emailInput);

        const mobileNoInput = createEditableInput(manager.mobileNo);
        row.children[2].innerHTML = '';
        row.children[2].appendChild(mobileNoInput);

        const saveButton = createButton('bi bi-check-lg', () => {
            onSaveManager(row, manager, {
                username: usernameInput.value,
                email: emailInput.value,
                mobileNo: mobileNoInput.value
            });
        }, 'save-button');

        const cancelButton = createButton('bi bi-x-lg', () => {
            onCancelEditProduct(row, manager);
        }, 'cancel-button');

        row.cells[3].innerHTML = '';
        row.cells[3].appendChild(saveButton);
        row.cells[3].appendChild(cancelButton);

        saveButton.style.marginRight = '12px';
    }


    // Function to handle the "Tick" button click for a product (to update API)
    function onSaveManager(row, manager, updatedManager) {
        console.log(`Updating manager with ID: ${manager.id}`);
        fetch(`http://localhost:8080/easyshopper_server2/updateManager?managerId=${manager.id}&username=${encodeURIComponent(updatedManager.username)}&email=${encodeURIComponent(updatedManager.email)}&mobileNo=${encodeURIComponent(updatedManager.mobileNo)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
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
                console.log('Update Profile Success:', data);
                alert("Updated Successfully")
                window.location.reload();
            })
            .catch(error => {
                console.error('Error:', error);

            });
    }

    function onCancelEditProduct(row, manager) {
        window.location.reload();
    }


    // Function to handle the "View" button click
    function onViewManager(managerId) {
        console.log(`Viewing manager with ID: ${managerId}`);
        window.location.href = `viewmanager.html?managerId=${managerId}`;
    }

    // Function to handle the "Remove" button click
    function onRemoveManager(managerId) {
        console.log(`Removing manager with ID: ${managerId}`);

        fetch(`http://localhost:8080/easyshopper_server2/removeManager?managerId=${managerId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    console.log('Manager removed successfully.');
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

    document.getElementById('createManagerButton').addEventListener('click', function (event) {
        window.location.href = `createmanager.html`;
    });
});

