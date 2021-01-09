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
    prc 3
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
    type 2
    prc 3
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 4
    prb 2
    x 36
    y 55
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 4
    prb 4
    x 103
    y 81
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 3
    prb 2
    x 100
    y 75
  ]
  node [
    id 7
    label "7"
    type 1
    prc 1
    ant 7
    prb 2
    x 118
    y 117
  ]
  node [
    id 8
    label "8"
    type 1
    prc 5
    ant 10
    prb 2
    x 106
    y 102
  ]
  edge [
    source 0
    target 5
    bandwith 521
    delay 118
  ]
  edge [
    source 1
    target 4
    bandwith 430
    delay 248
  ]
  edge [
    source 2
    target 6
    bandwith 707
    delay 255
  ]
  edge [
    source 3
    target 8
    bandwith 449
    delay 356
  ]
  edge [
    source 3
    target 7
    bandwith 342
    delay 467
  ]
]
