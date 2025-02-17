document.addEventListener("DOMContentLoaded", function () {
    const deleteButtons = document.querySelectorAll(".btn-danger");

    deleteButtons.forEach(button => {
        button.addEventListener("click", function (event) {
            const confirmDelete = confirm("Êtes-vous sûr de vouloir supprimer ce patient ?");
            if (!confirmDelete) {
                event.preventDefault();
            }
        });
    });
});
