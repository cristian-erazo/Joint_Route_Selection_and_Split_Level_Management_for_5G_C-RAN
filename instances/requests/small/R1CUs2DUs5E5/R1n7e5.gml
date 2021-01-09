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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 1
    prb 2
    x 59
    y 60
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 1
    prb 4
    x 82
    y 40
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 3
    prb 3
    x 83
    y 22
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 6
    prb 5
    x 35
    y 76
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 8
    prb 5
    x 98
    y 46
  ]
  edge [
    source 0
    target 3
    bandwith 833
    delay 347
  ]
  edge [
    source 0
    target 6
    bandwith 374
    delay 327
  ]
  edge [
    source 1
    target 5
    bandwith 123
    delay 386
  ]
  edge [
    source 1
    target 2
    bandwith 535
    delay 329
  ]
  edge [
    source 1
    target 4
    bandwith 944
    delay 483
  ]
]
