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
    type 1
    prc 5
    ant 6
    prb 2
    x 72
    y 110
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 9
    prb 3
    x 115
    y 29
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 9
    prb 2
    x 17
    y 23
  ]
  edge [
    source 0
    target 2
    bandwith 436
    delay 133
  ]
  edge [
    source 1
    target 3
    bandwith 791
    delay 457
  ]
  edge [
    source 1
    target 4
    bandwith 491
    delay 374
  ]
]
