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
    prc 5
    ant 10
    prb 3
    x 120
    y 114
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 8
    prb 5
    x 106
    y 42
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 5
    prb 6
    x 17
    y 75
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 7
    prb 2
    x 76
    y 38
  ]
  edge [
    source 0
    target 4
    bandwith 883
    delay 473
  ]
  edge [
    source 1
    target 6
    bandwith 754
    delay 447
  ]
  edge [
    source 2
    target 5
    bandwith 256
    delay 113
  ]
  edge [
    source 2
    target 3
    bandwith 128
    delay 457
  ]
]
