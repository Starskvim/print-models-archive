const elem2 = document.getElementById("myBarTask");
const elem3 = document.getElementById("barsTable");
const elem4 = document.getElementById("myBar2");

var invisibleLoadElem = document.getElementsByClassName("invisibleLoad")

const statsTable = document.getElementById("statsTable");
const statsModels = document.getElementById("statsModels");
const statsZIP = document.getElementById("statsZIP");
const statsOTH = document.getElementById("statsOTH");
const statsSIZE = document.getElementById("statsSIZE");
const statsAVG = document.getElementById("statsAVG");
const statsMED = document.getElementById("statsMED");


function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

getResponse = async () => {
    let response = await fetch("http://localhost:8081/archive/api/service/progress-task");

    if (response.ok) {
        let obj = await response.json()
        return obj
    } else {
        return -1
    }
}

getResponseStats = async () => {
    let response = await fetch("http://localhost:8081/archive/api/service/statistics");

    if (response.ok) {
        let obj = await response.json()
        return obj
    } else {
        return -1
    }
}

const getProgress = async () => {
    let progressResponse = await getResponse()
    let progress = progressResponse.currentCount
    let task = progressResponse.currentTask

    elem3.classList.remove("invisibleLoad")

    // console.log(progressOBJ)

    while (progress < 100) {
        progressResponse = await getResponse()
        progress = progressResponse.currentCount
        task = progressResponse.currentTask
        elem4.style.width = progress + "%";
        elem4.innerHTML = progress + "%";
        console.log(progress)
        console.log(progress + "%")
        elem2.innerHTML = task;
        await sleep(300)
    }
}

const stats = async () => {
    let statsDB = await getResponseStats()

    let Models = statsDB.totalModels
    let ZIP = statsDB.totalZIP
    let OTH = statsDB.totalOTH
    let SIZE = statsDB.totalSize
    let AVG = statsDB.ratioAvg
    let MED = statsDB.ratioMed

    statsModels.innerHTML = Models
    statsZIP.innerHTML = ZIP
    statsOTH.innerHTML = OTH
    statsSIZE.innerHTML = SIZE + " GB"
    statsAVG.innerHTML = AVG + " %"
    statsMED.innerHTML = MED + " %"

    statsTable.classList.remove("invisibleLoad")

}