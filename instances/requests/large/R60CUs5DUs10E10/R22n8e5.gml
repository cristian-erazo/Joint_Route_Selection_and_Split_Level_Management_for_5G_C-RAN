graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
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
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 4
    prb 5
    x 54
    y 16
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 6
    prb 4
    x 15
    y 47
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 8
    prb 6
    x 112
    y 69
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 4
    prb 5
    x 112
    y 70
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 5
    prb 6
    x 72
    y 85
  ]
  edge [
    source 0
    target 4
    bandwith 315
    delay 366
  ]
  edge [
    source 1
    target 5
    bandwith 984
    delay 278
  ]
  edge [
    source 1
    target 7
    bandwith 824
    delay 467
  ]
  edge [
    source 2
    target 6
    bandwith 250
    delay 465
  ]
  edge [
    source 2
    target 3
    bandwith 422
    delay 437
  ]
]
