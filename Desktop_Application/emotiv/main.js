//const Cortex = require('./lib/cortex')

//const client = new Cortex({})

//client.ready.then(() =>
//  client.init()
//  .queryHeadsets()
//  .then(headsets => console.log('headsets', headsets))
//)

process.env.GOOGLE_API_KEY = 'AIzaSyColuXtJS9PrNz-aCrsAZYK3yClQQ25-cs';
const { app, BrowserWindow } = require('electron')

let win

function createWindow () {

    win = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            nodeIntegration: true
        }
    })

    //win.webContents.openDevTools();
    //win.$ = $
    win.loadFile('renders/home.html')

    win.on('closed', () => {
        win = null
    })
}


app.on('ready', createWindow)

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit()
    }
})

app.on('activate', () => {
    if (win === null) {
        createWindow()
    }
})


// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.
