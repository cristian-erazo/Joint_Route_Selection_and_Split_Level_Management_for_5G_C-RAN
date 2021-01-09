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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 10
    prb 4
    x 82
    y 88
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 4
    prb 3
    x 103
    y 55
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 10
    prb 4
    x 55
    y 99
  ]
  edge [
    source 0
    target 3
    bandwith 581
    delay 205
  ]
  edge [
    source 1
    target 2
    bandwith 548
    delay 347
  ]
  edge [
    source 1
    target 4
    bandwith 848
    delay 485
  ]
]
