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
    prc 4
  ]
  node [
    id 3
    label "3"
    type 2
    prc 3
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 5
    prb 4
    x 30
    y 39
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 4
    prb 6
    x 68
    y 92
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 2
    prb 5
    x 69
    y 97
  ]
  node [
    id 7
    label "7"
    type 1
    prc 1
    ant 6
    prb 6
    x 88
    y 38
  ]
  edge [
    source 0
    target 5
    bandwith 161
    delay 475
  ]
  edge [
    source 1
    target 7
    bandwith 841
    delay 199
  ]
  edge [
    source 2
    target 6
    bandwith 956
    delay 396
  ]
  edge [
    source 3
    target 4
    bandwith 225
    delay 476
  ]
]
