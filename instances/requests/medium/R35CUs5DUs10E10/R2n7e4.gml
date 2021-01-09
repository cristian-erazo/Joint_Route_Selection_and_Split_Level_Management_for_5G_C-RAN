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
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 8
    prb 6
    x 50
    y 44
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 6
    prb 2
    x 24
    y 24
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 6
    prb 3
    x 67
    y 51
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 7
    prb 2
    x 71
    y 54
  ]
  edge [
    source 0
    target 4
    bandwith 328
    delay 323
  ]
  edge [
    source 1
    target 3
    bandwith 707
    delay 203
  ]
  edge [
    source 2
    target 5
    bandwith 211
    delay 122
  ]
  edge [
    source 2
    target 6
    bandwith 391
    delay 324
  ]
]
