# NodeJS

## Description

[Simplu3D-experiments](https://github.com/simplu3d/simplu3D-experiments#simplu3d-experiments) is provided as an npm module to simplify demonstrator building.

## Usage

```bash
# note that maven is required
npm install --save git+https://github.com/simplu3d/simplu3D-experiments.git#master
```

```js
const simplu3d = require('simplu3d-experiments');

simplu3d.run(
    'fr.ign.cogit.simplu3d.experiments.smartplu.data.DataPreparator',[
        'data/municipality/74042/parcelle.json',
        'data/municipality/74042/building'
    ]
);
```

