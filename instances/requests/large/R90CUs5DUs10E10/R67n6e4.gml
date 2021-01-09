graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
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
    type 1
    prc 5
    ant 9
    prb 4
    x 21
    y 40
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 6
    prb 3
    x 70
    y 105
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 2
    prb 6
    x 48
    y 28
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 7
    prb 5
    x 89
    y 103
  ]
  edge [
    source 0
    target 5
    bandwith 195
    delay 387
  ]
  edge [
    source 0
    target 2
    bandwith 787
    delay 126
  ]
  edge [
    source 1
    target 4
    bandwith 948
    delay 237
  ]
  edge [
    source 1
    target 3
    bandwith 806
    delay 237
  ]
]
