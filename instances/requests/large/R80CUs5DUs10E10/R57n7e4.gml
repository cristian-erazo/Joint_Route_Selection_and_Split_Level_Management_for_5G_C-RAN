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
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 6
    prb 2
    x 20
    y 91
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 2
    prb 6
    x 94
    y 71
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 10
    prb 5
    x 16
    y 57
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 5
    prb 4
    x 24
    y 39
  ]
  edge [
    source 0
    target 5
    bandwith 375
    delay 380
  ]
  edge [
    source 1
    target 3
    bandwith 697
    delay 445
  ]
  edge [
    source 2
    target 6
    bandwith 301
    delay 275
  ]
  edge [
    source 2
    target 4
    bandwith 784
    delay 266
  ]
]
