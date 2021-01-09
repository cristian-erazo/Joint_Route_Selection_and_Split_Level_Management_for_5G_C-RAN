graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 3
    prb 4
    x 45
    y 92
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 2
    prb 2
    x 38
    y 102
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 5
    prb 3
    x 68
    y 83
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 7
    prb 4
    x 68
    y 18
  ]
  edge [
    source 0
    target 5
    bandwith 434
    delay 305
  ]
  edge [
    source 1
    target 6
    bandwith 479
    delay 247
  ]
  edge [
    source 2
    target 4
    bandwith 230
    delay 123
  ]
  edge [
    source 2
    target 3
    bandwith 524
    delay 299
  ]
]
