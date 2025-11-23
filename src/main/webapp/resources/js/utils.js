export function updateDateTime() {
    const dateEl = document.getElementById("date");
    if (dateEl) {
        const now = new Date();
        const dateString = now.toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' });
        const timeString = now.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        $("#date").text(dateString);
        $("#time").text(timeString);
    }
}

export function parseR() {
    // Read from p:spinner.
    // PrimeFaces spinner usually has an input with id "formId:spinnerId_input".
    // Our ID is "coordinates-form:r-input".
    // So the input ID should be "coordinates-form:r-input_input".
    let input = document.getElementById("coordinates-form:r-input_input");
    if (!input) {
        // Fallback if not rendered as spinner yet or simple mode
        input = document.getElementById("coordinates-form:r-input");
    }

    let val = input ? input.value : 3;
    let parsed = parseFloat(val);
    if (isNaN(parsed)) return 3; // Default
    return parsed;
}