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
    type 1
    prc 5
    ant 8
    prb 2
    x 88
    y 23
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 5
    prb 2
    x 45
    y 106
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 5
    prb 5
    x 78
    y 96
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 6
    prb 4
    x 77
    y 87
  ]
  edge [
    source 0
    target 4
    bandwith 488
    delay 117
  ]
  edge [
    source 0
    target 3
    bandwith 845
    delay 350
  ]
  edge [
    source 1
    target 5
    bandwith 130
    delay 374
  ]
  edge [
    source 1
    target 2
    bandwith 503
    delay 160
  ]
]
