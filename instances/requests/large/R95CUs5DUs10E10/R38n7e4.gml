graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
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
    prc 1
    ant 2
    prb 4
    x 39
    y 17
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 10
    prb 2
    x 114
    y 26
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 5
    prb 5
    x 20
    y 40
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 4
    prb 6
    x 40
    y 65
  ]
  edge [
    source 0
    target 5
    bandwith 937
    delay 364
  ]
  edge [
    source 1
    target 4
    bandwith 703
    delay 134
  ]
  edge [
    source 2
    target 6
    bandwith 626
    delay 453
  ]
  edge [
    source 2
    target 3
    bandwith 341
    delay 284
  ]
]
