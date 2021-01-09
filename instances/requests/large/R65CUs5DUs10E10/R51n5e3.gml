graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 5
    prb 3
    x 89
    y 77
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 2
    prb 4
    x 65
    y 15
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 9
    prb 4
    x 92
    y 93
  ]
  edge [
    source 0
    target 4
    bandwith 216
    delay 348
  ]
  edge [
    source 1
    target 3
    bandwith 810
    delay 424
  ]
  edge [
    source 1
    target 2
    bandwith 622
    delay 267
  ]
]
