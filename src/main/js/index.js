const fs = require('fs');
const path = require('path');
const shell = require('shelljs');

const jarName = '/simplu3d-experiments-1.2-SNAPSHOT-shaded.jar';
const targetDir = path.resolve(__dirname, '../../../target/'+jarName);

function run(className, params){
    var cli = 'java -cp '
        + targetDir
        + ' ' + className
    ;
    for (i = 0; i < Object.keys(params).length; i++) {
        cli += ' ' + params[i];
    }
    console.log(cli);

    shell.exec(cli,
        function(code, stdout, stderr) {
            console.log('Exit code:', code);
            console.log('Program output:', stdout);
            console.log('Program stderr:', stderr);
    });
}

module.exports = {
    run: run
};
