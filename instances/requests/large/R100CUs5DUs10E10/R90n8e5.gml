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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 3
    prb 3
    x 42
    y 56
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 3
    prb 5
    x 32
    y 119
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 7
    prb 6
    x 97
    y 81
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 4
    prb 2
    x 38
    y 27
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 5
    prb 5
    x 75
    y 31
  ]
  edge [
    source 0
    target 4
    bandwith 318
    delay 375
  ]
  edge [
    source 1
    target 5
    bandwith 557
    delay 406
  ]
  edge [
    source 1
    target 3
    bandwith 369
    delay 300
  ]
  edge [
    source 2
    target 7
    bandwith 199
    delay 350
  ]
  edge [
    source 2
    target 6
    bandwith 958
    delay 313
  ]
]
