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
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 7
    prb 5
    x 17
    y 98
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 9
    prb 3
    x 30
    y 20
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 8
    prb 6
    x 22
    y 87
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 3
    prb 6
    x 50
    y 114
  ]
  edge [
    source 0
    target 6
    bandwith 200
    delay 488
  ]
  edge [
    source 1
    target 5
    bandwith 818
    delay 160
  ]
  edge [
    source 2
    target 3
    bandwith 289
    delay 406
  ]
  edge [
    source 2
    target 4
    bandwith 645
    delay 221
  ]
]
