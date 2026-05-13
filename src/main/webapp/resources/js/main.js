import { updateDateTime, parseR } from './utils.js';

import {
    setupCanvas,
    getCanvas,
    getContext,
    getWidth,
    getHeight,
    getDynamicScalingFactor,
    getK,
    getGraphSetup,
    setWidth,
    setHeight,
    setDynamicScalingFactor,
    setK
} from "./canvas-setup.js";

$(document).ready(() => {
    // updateDateTime(); // Clock is on start page
});

// window.onload removed to avoid conflict with main.xhtml inline script
// window.initCanvas is the entry point called by main.xhtml

// Initialize on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
    console.log("DOM fully loaded and parsed");
    if (window.initCanvas) {
        window.initCanvas();
    }
});

window.initCanvas = function () {
    console.log("Initializing canvas...");
    const r = parseR();
    setupCanvas(r);
    attachCanvasListeners();
    drawGraph(r);
    loadPointsAndDraw();
}

window.drawGraph = function drawGraph(R) {
    setDynamicScalingFactor(R);
    const { ctx, width, height, k, dynamicScalingFactor } = getGraphSetup();

    // Setup axes
    const yAxisOffset = 15;
    const xAxisStartX = (width / 2) - ((width / 4) * k);
    const xAxisEndX = (width / 2) + ((width / 4) * k);
    const yAxisStartY = (height / 2) + ((height / 4) * k);
    const yAxisEndY = (height / 2) - ((height / 4) * k);

    // Clear canvas
    ctx.clearRect(0, 0, width, height);
    ctx.font = "15px Arial";
    ctx.strokeStyle = "gray";
    ctx.lineWidth = 1;

    // Draw scaled axes
    drawAxis(ctx, xAxisStartX, height / 2, xAxisEndX, height / 2, k);  // X-axis
    drawAxis(ctx, width / 2, yAxisStartY, width / 2, yAxisEndY, k); // Y-axis

    ctx.fillStyle = "#3498db50"; // Blue with opacity
    ctx.strokeStyle = "#2980b9";

    // 1st Quadrant: Rectangle (x <= R, y <= R/2)
    // (0,0) to (R, R/2)
    ctx.beginPath();
    ctx.rect(width / 2, height / 2 - (R / 2) * dynamicScalingFactor, R * dynamicScalingFactor, (R / 2) * dynamicScalingFactor);
    ctx.fill();
    ctx.stroke();

    // 4th Quadrant: Triangle (y > x - R/2)
    // Vertices: (0,0), (0, -R/2), (R/2, 0)
    ctx.beginPath();
    ctx.moveTo(width / 2, height / 2); // (0,0)
    ctx.lineTo(width / 2, height / 2 + (R / 2) * dynamicScalingFactor); // (0, -R/2)
    ctx.lineTo(width / 2 + (R / 2) * dynamicScalingFactor, height / 2); // (R/2, 0)
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // 3rd Quadrant: Sector (x^2 + y^2 <= R^2/4) -> Radius R/2
    ctx.beginPath();
    ctx.moveTo(width / 2, height / 2);
    ctx.arc(width / 2, height / 2, (R / 2) * dynamicScalingFactor, 0.5 * Math.PI, Math.PI);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Draw labels
    ctx.fillStyle = "black";
    // X-axis labels
    ctx.fillText(R.toString(), width / 2 + R * dynamicScalingFactor, height / 2 + 20);
    ctx.fillText((R / 2).toString(), width / 2 + (R / 2) * dynamicScalingFactor, height / 2 + 20);
    ctx.fillText((-R).toString(), width / 2 - R * dynamicScalingFactor, height / 2 + 20);
    ctx.fillText((-R / 2).toString(), width / 2 - (R / 2) * dynamicScalingFactor, height / 2 + 20);
    ctx.fillText("X", width / 2 + (width / 2 - 20), height / 2 + 20);

    // Y-axis labels
    ctx.fillText(R.toString(), width / 2 + 10, height / 2 - R * dynamicScalingFactor);
    ctx.fillText((R / 2).toString(), width / 2 + 10, height / 2 - (R / 2) * dynamicScalingFactor);
    ctx.fillText((-R).toString(), width / 2 + 10, height / 2 + R * dynamicScalingFactor);
    ctx.fillText((-R / 2).toString(), width / 2 + 10, height / 2 + (R / 2) * dynamicScalingFactor);
    ctx.fillText("Y", width / 2 + 10, height / 2 - (height / 2 - 20));
}

export function drawAxis(context, fromX, fromY, toX, toY, k) {
    const headLength = 10;
    const angle = Math.atan2(toY - fromY, toX - fromX);

    context.beginPath();
    context.moveTo(fromX, fromY);
    context.lineTo(toX, toY);
    context.lineTo(toX - headLength * Math.cos(angle - Math.PI / 6), toY - headLength * Math.sin(angle - Math.PI / 6));
    context.moveTo(toX, toY);
    context.lineTo(toX - headLength * Math.cos(angle + Math.PI / 6), toY - headLength * Math.sin(angle + Math.PI / 6));
    context.stroke();
}

export function translateCanvasCoordsToReal(canvasX, canvasY) {
    const width = getWidth();
    const height = getHeight();
    const dynamicScalingFactor = getDynamicScalingFactor();

    let graphX = (canvasX - width / 2) / dynamicScalingFactor;
    let graphY = (height / 2 - canvasY) / dynamicScalingFactor;
    return { x: graphX, y: graphY };
}

export function translateRealCoordsToCanvas(x, y) {
    const width = getWidth();
    const height = getHeight();
    const dynamicScalingFactor = getDynamicScalingFactor();

    let canvasX = x * dynamicScalingFactor + width / 2;
    let canvasY = height / 2 - y * dynamicScalingFactor;
    return { canvasX, canvasY };
}

export function attachCanvasListeners() {
    const canvas = getCanvas();
    if (!canvas) {
        console.error("Canvas not found in attachCanvasListeners");
        return;
    }
    console.log("Attaching click listener to canvas");

    canvas.addEventListener("click", (e) => {
        const rect = canvas.getBoundingClientRect();
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;

        let canvasX = (e.clientX - rect.left) * scaleX;
        let canvasY = (e.clientY - rect.top) * scaleY;

        let { x, y } = translateCanvasCoordsToReal(canvasX, canvasY);

        // Send to server
        sendCoords(x, y);
    });
}

window.handlePointAddedFromServer = function() {
    if (!window.lastClickedPoint) {
        console.warn("No lastClickedPoint found");
        return;
    }
    const {x, y, r} = window.lastClickedPoint;

    window.drawDotOnCanvas(x, y, r, undefined, true);
}

window.sendCoords = function sendCoords(x, y) {
    let rInput = document.getElementById('coordinates-form:r-input_input');
    let r = rInput ? rInput.value : 1;
    r = ("" + r).replace(',', '.');

    console.log("Sending coords via commandScript:", x, y, r);
    window.lastClickedPoint = { x, y, r }; // For potential future use
    // Передаём параметры x, y, r так, чтобы PrimeFaces remoteCommand создал request parameters
    if (typeof addPointScript === 'function') {
        addPointScript([
            { name: 'x', value: x },
            { name: 'y', value: y },
            { name: 'r', value: r }
        ]);
    } else {
        console.error("addPointScript function is not defined!");
        alert("Error: addPointScript is not defined. Check console.");
    }
}

window.drawDotOnCanvas = function (x, y, r, result, isRealCoords = false, save = false) {
    const ctx = getContext();
    let canvasX, canvasY;

    if (isRealCoords) {
        ({ canvasX, canvasY } = translateRealCoordsToCanvas(x, y));
    } else {
        canvasX = x;
        canvasY = y;
    }

    ctx.fillStyle = result === undefined ? "gray" : result ? "#2ecc71" : "#e74c3c"; // Green/Red
    ctx.beginPath();
    ctx.arc(canvasX, canvasY, 4, 0, Math.PI * 2);
    ctx.fill();

    if (save) {
        savePointLocal(x, y, r, result);
    }
}

window.updateCanvasR = function (val) {
    // Called when R input changes (Spinner)
    const r = parseFloat(val);
    if (!isNaN(r)) {
        drawGraph(r);
        loadPointsAndDraw(); // Redraw points with new R scaling
    }
}

window.updateCanvas = function (r) {
    // Called from server ajax
    drawGraph(parseFloat(r));
    loadPointsAndDraw();
}

// LocalStorage Logic
function savePointLocal(x, y, r, result) {
    let points = JSON.parse(localStorage.getItem("graphPoints") || "[]");
    points.push({ x, y, r, result });
    localStorage.setItem("graphPoints", JSON.stringify(points));
}

function loadPointsAndDraw() {
    let points = JSON.parse(localStorage.getItem("graphPoints") || "[]");
    points.forEach(p => {
        // Draw without saving again
        // We pass 'true' for isRealCoords because stored points are real coords
        // We need to ensure we don't save it again inside drawDotOnCanvas if we add logic there
        // But drawDotOnCanvas is just drawing.
        // We should call it.
        window.drawDotOnCanvas(p.x, p.y, p.r, p.result, true);
    });
}
