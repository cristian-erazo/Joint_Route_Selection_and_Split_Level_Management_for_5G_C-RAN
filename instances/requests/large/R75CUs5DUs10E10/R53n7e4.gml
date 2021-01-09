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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 2
    prb 3
    x 72
    y 79
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 4
    prb 5
    x 48
    y 70
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 5
    prb 2
    x 109
    y 25
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 3
    prb 6
    x 19
    y 16
  ]
  edge [
    source 0
    target 3
    bandwith 532
    delay 468
  ]
  edge [
    source 1
    target 6
    bandwith 319
    delay 492
  ]
  edge [
    source 2
    target 4
    bandwith 586
    delay 288
  ]
  edge [
    source 2
    target 5
    bandwith 986
    delay 312
  ]
]
