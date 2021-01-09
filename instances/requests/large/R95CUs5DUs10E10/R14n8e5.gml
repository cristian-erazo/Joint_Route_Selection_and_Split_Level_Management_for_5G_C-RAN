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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 4
    prb 4
    x 74
    y 46
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 5
    prb 6
    x 100
    y 69
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 5
    prb 4
    x 31
    y 74
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 2
    prb 2
    x 25
    y 59
  ]
  node [
    id 7
    label "7"
    type 1
    prc 3
    ant 6
    prb 6
    x 67
    y 109
  ]
  edge [
    source 0
    target 5
    bandwith 100
    delay 176
  ]
  edge [
    source 1
    target 7
    bandwith 508
    delay 487
  ]
  edge [
    source 1
    target 4
    bandwith 629
    delay 459
  ]
  edge [
    source 2
    target 3
    bandwith 908
    delay 407
  ]
  edge [
    source 2
    target 6
    bandwith 415
    delay 271
  ]
]
