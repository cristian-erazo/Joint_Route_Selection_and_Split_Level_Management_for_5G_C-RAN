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
    type 1
    prc 5
    ant 8
    prb 4
    x 36
    y 78
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 5
    prb 3
    x 15
    y 89
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 3
    x 57
    y 45
  ]
  edge [
    source 0
    target 4
    bandwith 284
    delay 160
  ]
  edge [
    source 1
    target 3
    bandwith 614
    delay 277
  ]
  edge [
    source 1
    target 2
    bandwith 554
    delay 338
  ]
]
