var max = 99,
    colors = [
        "001219",
        "005f73",
        "0a9396",
        "94d2bd",
        "e9d8a6",
        "ee9b00",
        "ca6702",
        "bb3e03",
        "ae2012",
        "9b2226"
    ],
    shapes = [
        "M0,75 C0,31 31,0 75,0 S150,31 150,75 118,150 75,150 0,118 0,75",
        "M0,19 C0,13 13,0 19,0 S39,13 39,19 25,39 19,39 0,25 0,19"
    ];

function createShapes(n) {
    for (var i = 0; i < n; i++) {
        var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        stage.appendChild(path);
        gsap.fromTo(
            path,
            {
                x: gsap.utils.random(-50, innerWidth, 1),
                y: gsap.utils.random(-50, innerHeight, 1),
                scale: gsap.utils.random(0.5, 2.7),
                transformOrigin: "99 99",
                attr: {
                    class: "p",
                    d: randFromVarArr(shapes),
                    fill: "#" + randFromVarArr(colors)
                }
            },
            {
                scale: Math.random() > 0.5 ? "+=0.1" : "-=0.1",
                duration: gsap.utils.random(4, 6)
            }
        );
    }

    if (stage.childNodes.length > max) {
        while (stage.childNodes.length > max) {
            stage.childNodes[0].remove();
        }
    }
}

function randFromVarArr(arr) {
    return arr[gsap.utils.random(0, arr.length - 1, 1)];
}

function randBG() {
    gsap.set("body", {background: "#" + randFromVarArr(colors)});
}

window.onload = window.onresize = () => {
    randBG();
    createShapes(gsap.utils.random(25, 75, 1));
};

window.onclick = () => {
    createShapes(gsap.utils.random(25, 75, 1));
    // gsap.set(".p", {
    //     attr: {
    //         fill: () => "#" + randFromVarArr(colors)
    //     }
    // });
};

// window.onmousemove = () => {
// 	createShapes(5);
// };