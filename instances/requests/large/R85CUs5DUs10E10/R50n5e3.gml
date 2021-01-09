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
    prc 1
    ant 6
    prb 4
    x 37
    y 71
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 6
    prb 2
    x 91
    y 111
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 9
    prb 6
    x 108
    y 70
  ]
  edge [
    source 0
    target 3
    bandwith 380
    delay 367
  ]
  edge [
    source 1
    target 2
    bandwith 588
    delay 342
  ]
  edge [
    source 1
    target 4
    bandwith 560
    delay 407
  ]
]
