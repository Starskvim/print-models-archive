/*--------------------
Settings
--------------------*/
const o = {
    'width': Math.min(500, window.innerHeight * .9),
    'height': Math.min(500, window.innerHeight * .9),
    'lines': 33,
    'points': 70,
    'amplitudeX': 200,
    'amplitudeY': 17,
    'zoomX': 0.01,
    'zoomY': 0.0029,
    'seed': 0.65,
    'palette': 96,
    'dash': 6,
    'maxDash': 23,
    'strokeWidth': 1
};


/*--------------------
Vars
--------------------*/
const w3 = 'http://www.w3.org/2000/svg';
const $s = document.querySelector('svg');
const simplex = new SimplexNoise();
let palettes;

/*--------------------
Reset
--------------------*/
const reset = () => {
    $s.innerHTML = '';
    $s.setAttribute('viewbox', `0 0 ${o.width} ${o.height}`);
    $s.style.width = `${o.width}px`;
    $s.style.height = `${o.height}px`;
    $s.style.border = `${o.strokeWidth}px solid ${palettes[o.palette][Math.floor(Math.random() * 5)]}`;
};

/*--------------------
Create Lines
--------------------*/
const createLines = () => {
    const Lines = [];
    const distX = o.width / o.lines;
    const distY = o.height / o.points;

    for (let l = 0; l <= o.lines; l++) {
        const Points = [];
        for (let p = 0; p <= o.points; p++) {
            const noise = simplex.noise3D(l * o.zoomX, p * o.zoomY, o.seed);
            Points.push({
                x: distX * l + Math.sin(Math.PI * 2 * p / o.points) * noise * o.amplitudeX,
                y: distY * p + Math.sin(Math.PI * 2 * p / o.points) * noise * o.amplitudeY
            });

        }
        Lines.push({
            points: Points
        });

    }
    return Lines;
};

/*--------------------
Draw
--------------------*/
const draw = () => {
    reset();
    const lines = createLines();
    lines.forEach(l => {
        const path = document.createElementNS(w3, 'path');
        let d = '';
        l.points.forEach((p, i) => {
            d += i === 0 ? 'M' : 'L';
            d += ` ${p.x} ${p.y} `;
        });

        const offset = 1 + Math.floor(Math.random() * o.maxDash);
        path.setAttribute('d', d);
        path.style.strokeDasharray = `${offset} ${o.dash} ${offset * .5} ${o.dash * .5}`;
        path.style.setProperty('--offset', offset * 1.5 + o.dash * 1.5);
        path.style.stroke = palettes[o.palette][Math.floor(Math.random() * 5)];
        path.style.strokeWidth = o.strokeWidth;

        $s.appendChild(path);
    });
};

/*--------------------
Init
--------------------*/
fetch("https://cdn.jsdelivr.net/npm/nice-color-palettes@3.0.0/100.json").then(response => response.json()).then(data => {
    palettes = data;
    draw();
});

/*--------------------
Tweakpane
--------------------*/
const pane = new Tweakpane.Pane();
const f = pane.addFolder({
    title: 'Settings'
});

f.addInput(o, 'palette', {
    min: 0,
    max: 99,
    step: 1
});

f.addInput(o, 'lines', {
    min: 10,
    max: 100,
    step: 1
});

f.addInput(o, 'points', {
    min: 10,
    max: 100,
    step: 1
});

f.addInput(o, 'amplitudeX', {
    label: 'amp X',
    min: 0,
    max: 200,
    step: 1
});

f.addInput(o, 'amplitudeY', {
    label: 'amp Y',
    min: 0,
    max: 200,
    step: 1
});

f.addInput(o, 'zoomX', {
    amp: 'zoom X',
    min: 0,
    max: 0.03,
    step: .0001,
    format: v => v.toFixed(6)
});

f.addInput(o, 'zoomY', {
    amp: 'zoom Y',
    min: 0,
    max: 0.03,
    step: .0001,
    format: v => v.toFixed(6)
});

f.addInput(o, 'seed', {
    min: 0,
    max: 10,
    step: .01
});


f.addInput(o, 'dash', {
    min: 0,
    max: 30,
    step: 1
});

f.addInput(o, 'maxDash', {
    label: 'max dash',
    min: 0,
    max: 50,
    step: 1
});

f.addInput(o, 'strokeWidth', {
    label: 'stroke width',
    min: 1,
    max: 10,
    step: 1
});


pane.on('change', () => {
    draw();
    let output = `const o = ${JSON.stringify(o)}`;
    output = output.replace('{', '{\n\t');
    output = output.replace(/\"/g, '\'');
    output = output.replace(/\:/g, ': ');
    output = output.replace(/(?:,)/g, ',\n\t');
    output = output.replace('}', '\n}');
    console.log(output);
    // console.log(pane.exportPreset())
});

/*--------------------
Random
--------------------*/
$s.addEventListener('click', () => {
    o.palette = Math.floor(Math.random() * 100);
    o.maxDash = 5 + Math.floor(Math.random() * 25);
    o.maxDash = 20 + Math.floor(Math.random() * 30);
    o.seed = Math.random() * 10;
    o.lines = 20 + Math.floor(Math.random() * 30);
    draw();
    pane.refresh();
});